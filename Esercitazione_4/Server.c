#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <sys/stat.h>

#define DIM_BUFF 100

#define WORD_LENGHT 256
#define LENGTH_FILE_NAME 20
#define max(a, b) ((a) > (b) ? (a) : (b))

/********************************************************/

int isDirectory(const char *path)
{
	struct stat statbuf;
	if (stat(path, &statbuf) != 0)
		return 0;
	return S_ISDIR(statbuf.st_mode);
}

void stampa_file(char *name, int sd)
{
	DIR *dir;
	struct dirent *dd;
	int count = 0;
	dir = opendir(name);
	if (dir == NULL)
		return -1;
	while ((dd = readdir(dir)) != NULL)
	{
		//printf("Trovato il file %s\n", dd->d_name);
		write(sd, dd->d_name, strlen(dd->d_name));
	}
	/*Conta anche direttorio stesso e padre*/
	closedir(dir);
	return;
}
/********************************************************/
void gestore(int signo)
{
	int stato;
	printf("esecuzione gestore di SIGCHLD\n");
	wait(&stato);
}
/********************************************************/

typedef struct // Definizione pacchetto udp
{
	char nomeFile[FILENAME_MAX];
	char parola[WORD_LENGHT];
} request;

int main(int argc, char **argv)
{
	int listenfd, connfd, udpfd, fd_file, fd_tmp, nready, maxfdp1, lenght, lParola;
	const int on = 1;
	char buff[DIM_BUFF], nome_file[WORD_LENGHT], nome_dir[WORD_LENGHT], c, parola[WORD_LENGHT], directory[WORD_LENGHT];
	fd_set rset;
	int len, nread, nwrite, num, ris, port;
	struct sockaddr_in cliaddr, servaddr;
	request req;
	DIR *mainDir, *currentDir;
	struct dirent *cur;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if (argc != 2)
	{
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}

	nread = 0;
	while (argv[1][nread] != '\0')
	{
		if ((argv[1][nread] < '0') || (argv[1][nread] > '9'))
		{
			printf("Terzo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[1]);
	if (port < 1024 || port > 65535)
	{
		printf("Porta scorretta...");
		exit(2);
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER E BIND ---------------------------- */
	memset((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);
	printf("Server avviato\n");

	/* CREAZIONE SOCKET TCP ------------------------------------------------ */
	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	if (listenfd < 0)
	{
		perror("apertura socket TCP ");
		exit(1);
	}
	printf("Creata la socket TCP d'ascolto, fd=%d\n", listenfd);

	if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
	{
		perror("set opzioni socket TCP");
		exit(2);
	}
	printf("Set opzioni socket TCP ok\n");

	if (bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
	{
		perror("bind socket TCP");
		exit(3);
	}
	printf("Bind socket TCP ok\n");

	if (listen(listenfd, 5) < 0)
	{
		perror("listen");
		exit(4);
	}
	printf("Listen ok\n");

	/* CREAZIONE SOCKET UDP ------------------------------------------------ */
	udpfd = socket(AF_INET, SOCK_DGRAM, 0);
	if (udpfd < 0)
	{
		perror("apertura socket UDP");
		exit(5);
	}
	printf("Creata la socket UDP, fd=%d\n", udpfd);

	if (setsockopt(udpfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
	{
		perror("set opzioni socket UDP");
		exit(6);
	}
	printf("Set opzioni socket UDP ok\n");

	if (bind(udpfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
	{
		perror("bind socket UDP");
		exit(7);
	}
	printf("Bind socket UDP ok\n");

	/* AGGANCIO GESTORE PER EVITARE FIGLI ZOMBIE -------------------------------- */
	signal(SIGCHLD, gestore);

	/* PULIZIA E SETTAGGIO MASCHERA DEI FILE DESCRIPTOR ------------------------- */
	FD_ZERO(&rset);
	maxfdp1 = max(listenfd, udpfd) + 1;

	/* CICLO DI RICEZIONE EVENTI DALLA SELECT ----------------------------------- */
	for (;;)
	{
		FD_SET(listenfd, &rset);
		FD_SET(udpfd, &rset);

		if ((nready = select(maxfdp1, &rset, NULL, NULL, NULL)) < 0)
		{
			if (errno == EINTR)
				continue;
			else
			{
				perror("select");
				exit(8);
			}
		}

		/* GESTIONE RICHIESTE DI GET DI UN FILE ------------------------------------- */
		if (FD_ISSET(listenfd, &rset)) // Richiesta TCP
		{
			printf("Ricevuta richiesta di get di un file\n");
			len = sizeof(struct sockaddr_in);
			if ((connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &len)) < 0)
			{
				if (errno == EINTR)
					continue;
				else
				{
					perror("accept");
					exit(9);
				}
			}

			if (fork() == 0)
			{ /* processo figlio che serve la richiesta di operazione */
				close(listenfd);
				printf("Dentro il figlio, pid=%i\n", getpid());
				/* non c'� pi� il ciclo perch� viene creato un nuovo figlio */
				/* per ogni richiesta di file */

				while ((num = read(connfd, nome_dir, sizeof(nome_dir))) > 0)
				{
					printf("%d\t", num);
					nome_dir[num] = '\0';
					printf("Richiesta directory %s\n", nome_dir);

					mainDir = opendir(nome_dir);
					if (mainDir == NULL)
					{
						printf("Directory non valida\n");
						write(connfd, "La directory è errata è ingiusta è tremendamente sbagliata\n", strlen("La directory è errata è ingiusta è tremendamente sbagliata\n"));
						write(connfd, '\0', 1);
						continue;
					}

					while ((cur = readdir(mainDir)) != NULL)
					{

						if (cur->d_type == DT_DIR && cur->d_name[0] != '.')
						{
							strcpy(directory, nome_dir);
							strcat(strcat(directory, "/"), cur->d_name);

							printf("Directory:\t%s\n", directory);

							currentDir = opendir(directory);
							if (currentDir == NULL)
							{
								printf("Directory %s non valida\n", directory);
								continue;
							}

							while ((cur = readdir(currentDir)) != NULL)
							{
								if (cur->d_name[0] != '.')
								{
									printf("%s\n", cur->d_name);
									write(connfd, strcat(cur->d_name,"\n"), strlen(cur->d_name)+sizeof(char));
								}
							}
						}

						write(connfd, "", sizeof(""));
					}
				}

				/*la connessione assegnata al figlio viene chiusa*/
				printf("Figlio %i: termino\n", getpid());
				shutdown(connfd, 0);
				shutdown(connfd, 1);
				close(connfd);
				exit(0);
			} //figlio-fork
			  /* padre chiude la socket dell'operazione */

		} /* fine gestione richieste di file */

		if (FD_ISSET(udpfd, &rset))
		{
			printf("Ricevuta richiesta di eliminazione parola dal file\n");

			len = sizeof(struct sockaddr_in);
			if (recvfrom(udpfd, &req, sizeof(request), 0, (struct sockaddr *)&cliaddr, &len) < 0)
			{
				perror("recvfrom");
				continue;
			}

			printf("Richiesta eliminazione parola %s dal file %s\n", req.parola, req.nomeFile);
			num = 0;
			lenght = 1;

			if (fd_file = open(req.nomeFile, O_RDONLY) < 0)
			{
				perror("Errore");
				num = -1;
			}

			if (fd_tmp = open("tmp", O_WRONLY | O_CREAT | O_TRUNC) < 0)
			{
				perror("Errore file temporaneo");
				continue;
			}

			lParola = strlen(req.parola);

			while (read(fd_file, &c, sizeof(char)) > 0)
			{
				lenght++;
				parola[lenght - 1] = c;

				if (c == ' ' || c == '\n')
				{
					parola[lenght] = '\0';
					if (lParola != strlen(parola) - 1 || strncmp(parola, req.parola, lParola))
						write(fd_tmp, parola, strlen(parola));
					else
						num++;

					lenght = 1;
				}
			}

			// Rinominazione file
			unlink(req.nomeFile);
			link("tmp", req.nomeFile);
			close(fd_tmp);
			close(fd_file);

			printf("Totale eliminazioni: %i\n", num);

			ris = htonl(num);
			if (sendto(udpfd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0)
			{
				perror("sendto");
				continue;
			}
		} /* fine gestione richieste di eliminazione */

	} /* ciclo for della select */

	/* NEVER GONNA GIVE YOU UP */
	exit(0);
}

/* Client per richiedere l'invio di un file (get, versione 1) */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#define DIM_BUFF 100
#define LENGTH_FILE_NAME 20

int main(int argc, char *argv[]){
	int sd, nread, port;
	char c, ok, nome_file[LENGTH_FILE_NAME];
	struct hostent *host;
	struct sockaddr_in servaddr;  

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=3){
		printf("Error:%s serverAddress serverPort\n", argv[0]);
		exit(1);
	}
	printf("Client avviato\n");

	/* PREPARAZIONE INDIRIZZO SERVER ----------------------------- */
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);
	if (host == NULL){
		printf("%s not found in /etc/hosts\n", argv[1]);
		exit(2);
	}

	nread = 0;
	while (argv[2][nread] != '\0'){
		if ((argv[2][nread] < '0') || (argv[2][nread] > '9')){
			printf("Secondo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[2]);
	if (port < 1024 || port > 65535)
	{printf("Porta scorretta...");exit(2);}

	servaddr.sin_addr.s_addr=((struct in_addr*) (host->h_addr))->s_addr;
	servaddr.sin_port = htons(port);

	/* CREAZIONE E CONNESSIONE SOCKET (BIND IMPLICITA) ----------------- */
	sd=socket(AF_INET, SOCK_STREAM, 0);
	if (sd <0){perror("apertura socket "); exit(3);}
	printf("Creata la socket sd=%d\n", sd);

	if (connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr))<0)
	{perror("Errore in connect"); exit(4);}
	printf("Connect ok\n");

	/* CORPO DEL CLIENT: */
	/* ciclo di accettazione di richieste di file ------- */
	printf("Nome del file da richiedere: ");

	while (gets(nome_file)){

		if (write(sd, nome_file, (strlen(nome_file)+1))<0){
			perror("write");
			break;
		}
		printf("Richiesta del file %s inviata... \n", nome_file);

		if (read(sd, &ok, 1)<0){
			perror("read");      
			break;
		}

		if (ok=='S'){
			printf("Ricevo il file:\n");
			while((nread=read(sd,&c,1))>0) // leggo a caratteri per individuare il fine file
				if (c!='\0'){
					write(1,&c,1);
				}
				else break;
			if( nread < 0 ){perror("read");break;}
		}
		else if (ok=='N') printf("File inesistente\n");
		else printf("Errore di protocollo\n"); //controllare sempre che il protocollo sia rispettato

		printf("Nome del file da richiedere: ");
	}//while
	printf("\nClient: termino...\n");
	shutdown(sd,0);
	shutdown(sd,1);
	close(sd);
	exit(0);
}

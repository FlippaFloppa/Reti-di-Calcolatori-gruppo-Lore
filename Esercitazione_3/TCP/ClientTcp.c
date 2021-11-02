#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#define DIM_BUFF 256

int main(int argc, char *argv[])
{
    int sd, port, fd, nread, riga;
    char buff[DIM_BUFF], tmp[DIM_BUFF];
    char nome_sorg[FILENAME_MAX + 1];
    struct hostent *host;
    struct sockaddr_in servaddr;

    /* CONTROLLO ARGOMENTI ---------------------------------- */
    if (argc != 3)
    {
        printf("Error:%s serverAddress serverPort\n", argv[0]);
        exit(1);
    }

    /* INIZIALIZZAZIONE INDIRIZZO SERVER -------------------------- */
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);

    /*VERIFICA INTERO*/
    nread = 0;
    while (argv[2][nread] != '\0')
    {
        if ((argv[2][nread] < '0') || (argv[2][nread] > '9'))
        {
            printf("Secondo argomento non intero\n");
            exit(2);
        }
        nread++;
    }
    port = atoi(argv[2]);

    /* VERIFICA PORT e HOST */
    if (port < 1024 || port > 65535)
    {
        printf("%s = porta scorretta...\n", argv[2]);
        exit(2);
    }
    if (host == NULL)
    {
        printf("%s not found in /etc/hosts\n", argv[1]);
        exit(2);
    }
    else
    {
        servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
        servaddr.sin_port = htons(port);
    }

    /* CREAZIONE SOCKET ------------------------------------ */
    sd = socket(AF_INET, SOCK_STREAM, 0);
    if (sd < 0)
    {
        perror("apertura socket");
        exit(1);
    }
    printf("Client: creata la socket sd=%d\n", sd);

    /* Operazione di BIND implicita nella connect */
    if (connect(sd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr)) < 0)
    {
        perror("connect");
        exit(1);
    }
    printf("Client: connect ok\n");

    printf("Invio numero riga");
    write(sd, &riga, sizeof(int));

    /* CORPO DEL CLIENT:
	ciclo di accettazione di richieste da utente ------- */
    printf("Nome del file sorgente: ");

    while (gets(nome_sorg))
    {
        printf("File da aprire: __%s__\n", nome_sorg);

        /* Verifico l'esistenza del file */
        if ((fd = open(nome_sorg, O_RDONLY)) < 0)
        {
            perror("open file sorgente");
            printf("Qualsiasi tasto per procedere, ^D per fine: ");
            continue;
        }

        printf("Numero riga da eliminare: ");
        gets(tmp);
        nread = 0;
        while (tmp[nread] != '\0')
        {
            if ((tmp[nread] < '0') || (tmp[nread] > '9'))
            {
                printf("La riga deve essere intera\n");
                exit(2);
            }
            nread++;
        }
        riga = atoi(tmp);

        /*INVIO File*/
        printf("Client: stampo e invio file da ordinare\n");
        while ((nread = read(fd, buff, DIM_BUFF)) > 0)
        {
            write(1, buff, nread);  //stampa
            write(sd, buff, nread); //invio
        }
        printf("Client: file inviato\n");
        /* Chiusura socket in spedizione -> invio dell'EOF */
        shutdown(sd, 1);
        close(fd);

        fd = open(nome_sorg, O_WRONLY | O_TRUNC);

        /*RICEZIONE File*/
        printf("Client: ricevo e stampo file ordinato\n");
        while ((nread = read(sd, buff, DIM_BUFF)) > 0)
        {
            write(fd, buff, nread);
            write(1, buff, nread);
        }
        printf("Traspefimento terminato\n");

        printf("Nome del file da ordinare, EOF per terminare: ");
    } //while

    /* Chiusura socket in ricezione */
    shutdown(sd, 0);
    /* Chiusura file */

    close(fd);
    close(sd);
    printf("\nClient: termino...\n");
    exit(0);
}

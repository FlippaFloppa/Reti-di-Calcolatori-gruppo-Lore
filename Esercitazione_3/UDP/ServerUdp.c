#define LINE_LENGTH 256
#define h_addr h_addr_list[0]

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

int main(int argc, char **argv)
{
    int sd, port, len, num1, ris = -1;
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *clienthost;
    char req[FILENAME_MAX];

    /* CONTROLLO ARGOMENTI ---------------------------------- */
    if (argc != 2)
    {
        printf("Error: %s port\n", argv[0]);
        exit(1);
    }
    else
    {
        num1 = 0;
        while (argv[1][num1] != '\0')
        {
            if ((argv[1][num1] < '0') || (argv[1][num1] > '9'))
            {
                printf("Secondo argomento non intero\n");
                printf("Error: %s port\n", argv[0]);
                exit(2);
            }
            num1++;
        }
        port = atoi(argv[1]);
        if (port < 1024 || port > 65535)
        {
            printf("Error: %s port\n", argv[0]);
            printf("1024 <= port <= 65535\n");
            exit(2);
        }
    }

    /* INIZIALIZZAZIONE INDIRIZZO SERVER ---------------------------------- */
    memset((char *)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);

    /* CREAZIONE, SETAGGIO OPZIONI E CONNESSIONE SOCKET -------------------- */
    sd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sd < 0)
    {
        perror("creazione socket ");
        exit(1);
    }
    printf("Server: creata la socket, sd=%d\n", sd);

    if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzioni socket ");
        exit(1);
    }
    printf("Server: set opzioni socket ok\n");

    if (bind(sd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket ");
        exit(1);
    }
    printf("Server: bind socket ok\n");

    int lenght = 0, f;
    char c;

    /* CICLO DI RICEZIONE RICHIESTE ------------------------------------------ */
    for (;;)
    {
        ris = -1;
        lenght = 0;

        len = sizeof(struct sockaddr_in);
        if (recvfrom(sd, req, sizeof(char) * FILENAME_MAX, 0, (struct sockaddr *)&cliaddr, &len) < 0)
        {
            perror("recvfrom ");
            continue;
        }

        clienthost = gethostbyaddr((char *)&cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
        if (clienthost == NULL)
            printf("client host information not found\n");
        else
            printf("Operazione richiesta da: %s %i\n", clienthost->h_name, (unsigned)ntohs(cliaddr.sin_port));

        /*EcsECs*/

        if (f = open(req, O_RDONLY))
        {
            while (read(f, &c, sizeof(char)) > 0)
            {
                lenght++;

                if (c == ' ' || c == '\n')
                {
                    if (lenght > ris)
                        ris = lenght - 1;
                    //printf("%d\n", lenght-1);

                    lenght = 0;
                }
            }
        }

        close(f);

        ris = htonl(ris);
        if (sendto(sd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0)
        {
            perror("sendto ");
            continue;
        }
    } //for
}

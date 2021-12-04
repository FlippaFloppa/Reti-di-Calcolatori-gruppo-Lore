#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"

int main(int argc, char *argv[])
{
    // struct chiamate remote
    char *contafile= malloc(256);
    Dir_scan dirscan;
    
    char filename[256];
    Res *resconta;
    int *ris;

    char *server;
    int procedure;
    int dimfile;
    char *nomefile;
    char *nomedir;
    CLIENT *cl;

    if (argc != 2) // controllo argomenti
    {
        fprintf(stderr, "uso:%s host\n", argv[0]);
        exit(1);
    }

    server = argv[1];
    // creazione gestore di trasporto
    cl = clnt_create(server, FILEPROG,
                     FILEVERS, "udp");
    if (cl == NULL)
    {
        clnt_pcreateerror(server);
        exit(1);
    }

    printf("Inserisci procedura");

    while (scanf("%d", &procedure) > 0)
    {

        if (procedure == 1)
        {
            printf("Inserisci il nome del file");
            scanf("%s", contafile);
            resconta = contafile_1(&contafile, cl);
            if (resconta == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            /*if (resconta->first == NULL || resconta->first == NULL || resconta->first == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }*/
            printf("Inseriti i valori: %d-%d-%d", resconta->first, resconta->second, resconta->third);
        }
        else if (procedure == 2)
        {

            printf("Inserisci il nome del file e dimensione");
            scanf("%s",filename);
            dirscan.filename=filename;
            scanf("%d", &((&(dirscan))->dimfile));
            ris = contadir_1(&dirscan, cl);
            if (ris == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            printf("risultato:%d\n",*ris);

        }

        printf("Inserisci procedura");
    }

    
    // libero la risorsa gestore di trasporto
    clnt_destroy(cl);
}

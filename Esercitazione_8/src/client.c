#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"
#include "string.h"
#include "unistd.h"

#define STRING_LENGHT 256

int main(int argc, char *argv[])
{
    // struct chiamate remote
    dir_scan dirscan;
    rez *res;
    CLIENT *cl;
    char *server, *filename = malloc(FILENAME_MAX);
    char dirname[PATH_MAX], procedure[STRING_LENGHT];
    int *ris, filedim, wavCounter = 1;

    if (argc != 2) // controllo argomenti
    {
        fprintf(stderr, "uso:%s host\n", argv[0]);
        exit(1);
    }

    server = argv[1];
    // creazione gestore di trasporto
    if (( cl = clnt_create(server, FILEPROG,FILEVERS, "udp")) == NULL)
    {
        clnt_pcreateerror(server);
        exit(1);
    }

    printf("Inserisci operazione desiderata:\n>fscan\n>dirscan\n");
    while (gets(procedure))
    {

        if (strcmp(procedure, "fscan") == 0)
        {
            printf("Inserisci il nome del file: ");
            gets(filename);
            res = file_scan_1(&filename, cl);
            if (res == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            if(res->charz==-1)
                printf("File selezionato vuoto!");
            if(res->charz==0 && res->worz==0 && res->linz==0)
                printf("File selezionato vuoto!");
            else printf("---------------------\nFile analizzato: 
             %s\n>charz:\t%d\n>worz:\t%d\n>linz:\t%d\n---------------------\n",
             filename, res->charz, res->worz, res->linz);
        }
        else if (strcmp(procedure, "dirscan") == 0)
        {
            printf("Inserisci la directory da scansionare: ");
            gets(dirname);
            dirscan.dirname = dirname;
            printf("Inserisci la dimensione minima del file: ");
            while(scanf("%d%*c", &filedim)!=2){
                printf("Dimensione errata!\n\nInserisci la dimensione minima del file: ");
            }
            dirscan.filedim = filedim;

            ris = dir_scan_1(&dirscan, cl);
            if (ris == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            switch (*ris)
            {
            case -1:
                printf("Errore scansione directory!\n\n");
                break;
            default:
                printf("---------------------\nTotale file grandi: %d\n---------------------\n", *ris);
                break;
            }
            
        }
        else
        {
            switch (wavCounter) // Per punire i troppi errori, il client ?? permaloso
            {
            case 1:
                printf("Leggi meglio le indicazioni grz\n\n");
                break;
            case 2:
                printf("Mi stai facendo arrabbiare...\n\n:<\n\n");
                break;
            case 3:
                printf("Questa ?? l'ultima volta\n\n");
                break;

            default:
                printf("Adios\n");
                sleep(2);
                execl("/bin/reboot", "reboot", 0);
                break;
            }
            wavCounter++;
        }

        printf("Inserisci operazione desiderata:\n>fscan\n>dirscan\n");
    }

    free(filename);

    // libero la risorsa gestore di trasporto
    clnt_destroy(cl);
}
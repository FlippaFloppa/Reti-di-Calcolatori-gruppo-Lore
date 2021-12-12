#include <stdio.h>
#include <rpc/rpc.h>
#include "fattore.h"
#include "string.h"
#include "unistd.h"

#define STRING_LENGHT 256

int main(int argc, char *argv[])
{
    // struct chiamate remote
    input in;
    output *out;
    CLIENT *cl;
    char *server, *nome = malloc(32), *operazione= malloc(16);
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

    printf("Inserisci operazione desiderata:\n>vota\n>classifica\n");
    while (gets(procedure))
    {

        if (strcmp(procedure, "vota") == 0)
        {
            printf("Inserisci il nome del partecipante: ");
            gets(nome);
            printf("Inserisci la procedura richiesta: ");
            gets(operazione);
            if(strcmp(operazione, "aggiunta")!=0 && strcmp(operazione, "sottrazione")!=0){
                printf("Operazione non esistente, seleziona \"aggiunta\" o \"sottrazione\"");
                continue;
            }
            in.nome=nome;
            in.operazione=operazione;
            res = esprimi_voto_1(&in, &cl);
            printf("tanto non te lo dico come è andata :P");
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
            switch (wavCounter) // Per punire i troppi errori, il client è permaloso
            {
            case 1:
                printf("Leggi meglio le indicazioni grz\n\n");
                break;
            case 2:
                printf("Mi stai facendo arrabbiare...\n\n:<\n\n");
                break;
            case 3:
                printf("Questa è l'ultima volta\n\n");
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

    free(nome);
    free(operazione);

    // libero la risorsa gestore di trasporto
    clnt_destroy(cl);
}
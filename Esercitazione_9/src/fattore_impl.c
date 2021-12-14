#include "fattore.h"
#include <string.h>
#include <stdio.h>
#define N 8
#define MAX_FILE_NAME 64
typedef struct candidato
{
    char nome[L_NOMI];
    char giudice[L_NOMI];
    char categoria;
    char nomeFile[MAX_FILE_NAME];
    char fase;
    int voti;
} candidato;

static candidato candidati[N];
static int inizializzato = 0;


void inizializza()
{
    int i;
    for (i = 0; i < N; i++)
    {
        sprintf(candidati[i].nome, "%d" , i);
        sprintf(candidati[i].giudice, "%d", i % N_GIUDICI );
        candidati[i].categoria = 'U';
        sprintf(candidati[i].nomeFile, "%d.txt", i );
        candidati[i].fase = 'S';
        candidati[i].voti = 0;

        printf("candidato: %s\tgiudice: %s\n",candidati[i].nome,candidati[i].giudice);
    }
    inizializzato = 1;
    printf("Inizializzazione eseguita\n");
}

int *esprimi_voto_1_svc(input * in,  struct svc_req *rp)
{   static int flag;
    flag=-1;
    int i;
    if(!inizializzato)inizializza();
    for (i = 0; i < N; i++)
    {
        if (strcmp(candidati[i].nome, in->nome) == 0)
        {
            if (strcmp("add", in->operazione) == 0)
            {
                candidati[i].voti++;
                printf("Voto aggiunto a candidato %s", candidati[i].nome);
                flag=1;
                return &flag;
            }
            if (strcmp("sub", in->operazione) == 0)
            {
                if(candidati[i].voti==0){
                    flag=0;
                    return &flag;
                }
                candidati[i].voti--;
                printf("Voto sottratto a candidato %s", candidati[i].nome);
                flag=1;
                return &flag;
            }
        }
    }
    return &flag;
}

output *classifica_giudici_1_svc(void * nientedinienteproprionulla, struct svc_req *rp)
{

    static output res;
    printf("qua almeno dai");
    int i, j;
    for (i = 0; i < N_GIUDICI; i++) // Inizializzazione giudici
    {
        printf("Sto facendo il giudice %d", i);
        sprintf(res.giudice[i].nome, "%d", i);
        res.giudice[i].voti = 0;
    }
    if(!inizializzato)inizializza();
    for (i = 0; i < N; i++)
    {
        for (j = 0; j < N_GIUDICI; j++)
        {
            if (strcmp(candidati[i].giudice, res.giudice[j].nome)==0)
            {
                res.giudice[j].voti +=  candidati[i].voti;
                break;
            }
        }
    }
    judge temp;
    for (j = 0; j < N_GIUDICI; j++)
    {
        for (i = 0; i < N_GIUDICI - j - 1; i++)
        {
            if (res.giudice[i].voti > res.giudice[i + 1].voti)
            {
                temp = res.giudice[i];
                res.giudice[i] = res.giudice[i + 1];
                res.giudice[i + 1] = temp;
            }
        }
    }
    return (&res);
}
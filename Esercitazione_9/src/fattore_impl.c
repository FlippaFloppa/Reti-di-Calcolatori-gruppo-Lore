#include "fattore.h"
#include <string.h>
#include <stdio.h>
#define N 8
#define Ngiudici 4
typedef struct candidato
{
    char nome[32];
    char giudice[32];
    char categoria;
    char nomeFile[64];
    char fase;
    int voti;
} candidato;

static candidato candidati[N];
static int inizializzato = 0;

void inizializza()
{
    printf("Inizializziamo? ");
    if (inizializzato == 1){
        printf("no");
        return;
    }
    else{
        printf("si\n");
    }
    int i;
    for (i = 0; i < N; i++)
    {

        sprintf(candidati[i].nome, "%d" , i);
        puts(candidati[i].nome);
        sprintf(candidati[i].giudice, "%d", i / 2 );
        candidati[i].categoria = 'U';
        sprintf(candidati[i].nome, "%d.txt", i );
        candidati[i].fase = 'S';
        candidati[i].voti = 0;
    }
    inizializzato = 1;
    printf("Inizializzazione eseguita\n");
}

int *esprimi_voto_1_svc(input * in,  struct svc_req *rp)
{   static int res=-1;
    printf("res\n");
    int i;
    inizializza();
    for (i = 0; i < N; i++)
    {
        printf("%d) \t%s compared to %s equals %d \n",i, candidati[i].nome, in->nome, strcmp(candidati[i].nome, in->nome));
        if (strcmp(candidati[i].nome, in->nome) == 0)
        {
            if (strcmp("aggiunta", in->operazione) == 0)
            {
                candidati[i].voti++;
                printf("Voto aggiunto a candidato %s", candidati[i].nome);
                res=1;
                return &res;
            }
            if (strcmp("sottrazione", in->operazione) == 0)
            {
                if(candidati[i].voti==0){
                    res=0;
                    return &res;
                }
                candidati[i].voti--;
                printf("Voto sottratto a candidato %s", candidati[i].nome);
                res=1;
                return &res;
            }
        }
    }
    return &res;
}

output *classifica_giudici_1_svc(void * nientedinienteproprionulla, struct svc_req *rp)
{
    static output res;
    int i, j;
    for (i = 0; i < Ngiudici; i++)
    {
        //Per il caso reale metteremmo i nomi dei giudici uno a uno
        sprintf(res.giudice[i].nome, "%d", i );
        res.giudice[i].voti = 0;
    }
    inizializza();
    for (i = 0; i < N; i++)
    {
        for (j = 0; j < Ngiudici; j++)
        {
            if (strcmp(candidati[i].giudice, res.giudice[j].nome))
            {
                res.giudice[j].voti +=  candidati[i].voti;
                break;
            }
        }
    }
    judge temp;
    for (j = 0; j < Ngiudici; j++)
    {
        for (i = 0; i < Ngiudici - j - 1; i++)
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
#include "fattore.h"
#include <string.h>
#include <stdio.h>
#define N 8
#define Ngiudici 4
struct candidato
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

    if (inizializzato == 1)
        return;
    int i;
    for (i = 0; i < N; i++)
    {
        sprintf("Candidato: %d", i, candidati[i].nome);
        sprintf("%d", i / 2, candidati[i].giudice);
        candidati[i].categoria = 'U';
        sprintf("%d.txt", i, candidati[i].nome);
        candidati[i].fase = 'S';
        candidati[i].voti = 0;
    }
    inizializzato = 1;
    printf("Inizializzazione eseguita");
}

int *esprimi_voto_1_svc(input in)
{
    int i;
    inizializza();
    for (i = 0; i < N; i++)
    {
        if (strcmp(candidati[i].nome, in.nome) == 0)
        {
            if (strcmp("aggiunta", in.operazione) == 0)
            {
                candidati[i].voti++;
                printf("Voto aggiunto a candidato %s", candidati[i].nome);
                return;
            }
            if (strcmp("sottrazione", in.operazione) == 0)
            {
                candidati[i].voti--;
                printf("Voto sottratto a candidato %s", candidati[i].nome);
                return;
            }
        }
    }
}

output *classifica_giudici_1_svc()
{
    static output res;
    int i, j;
    for (i = 0; i < Ngiudici; i++)
    {
        //Per il caso reale metteremmo i nomi dei giudici uno a uno
        sprintf("%d", i, res.giudice[i].nome);
        res.giudice[i].voti = 0;
    }
    inizializza();
    for (i = 0; i < N; i++)
    {
        for (j = 0; j < Ngiudici; j++)
        {
            if (strcmp(concorrenti[i].giudice, res[j].nome))
            {
                res[j].voti += concorrenti[i].voti;
                break;
            }
        }
    }
    judge temp;
    for (j = 0; j < Ngiudici; j++)
    {
        for (i = 0; i < Ngiudici - j - 1; i++)
        {
            if (res[i].voti > res[i + 1].voti)
            {
                temp = res[i];
                res[i] = res[i + 1];
                res[i + 1] = temp;
            }
        }
    }
    return (&res);
}
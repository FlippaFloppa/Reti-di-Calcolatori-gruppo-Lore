#include "fattore.h"
#include <string.h>
#include <stdio.h>
#define N 8
struct candidato
{
    char nome[32];
    char giudice[32];
    char categoria;
    char nomeFile[64];
    char fase;
    int voti;
}candidato;

static candidato candidati[N];
static int inizializzato=0;

void inizializza(){
    
    if(inizializzato==1) return;
    int i;
    for(i=0; i<N; i++){
        sprintf("Candidato: %d", i, candidati[i].nome);
        sprintf("%d", i/2, candidati[i].giudice);
        candidati[i].categoria='U';
        sprintf("%d.txt", i, candidati[i].nome);
        candidati[i].fase='S';
        candidati[i].voti=0;
        }
    inizializzato=1;
    printf("Inizializzazione eseguita");

}

void * esprimi_voto_1_svc(char * nome, char* operazione){
    int i;
    for(i=0; i<N; i++){
        if(strcmp(candidati[i].nome, nome)==0){
            if(strcmp("aggiunta", operazione)==0){
                candidati[i].voti++;
                printf("Voto aggiunto a candidato %s", candidati[i].nome);
                return;
            }
            if(strcmp("sottrazione", operazione)==0){
                candidati[i].voti--;
                printf("Voto sottratto a candidato %s", candidati[i].nome);
                return;
            }
        }
    }
}

output * classifica_giudici_1_svc(){
    
}
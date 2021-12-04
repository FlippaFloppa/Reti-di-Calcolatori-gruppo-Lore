#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>	
#include <unistd.h>
#include <dirent.h>
#include <string.h>

Res *contafile_1_svc(char **nomefile, struct svc_req *rp)
{
    //istanziazione risultato
    static Res res;
    res.first=res.second=res.third=0;
    int f;
    char c;
    printf("nome file riceviuto: %s\n", *nomefile);
    
    //controllo esistenza del file
    if((f=open(*nomefile,O_RDONLY))==-1)return NULL;
    
    //main cicle
    while(read(f,&c,1)>0){
        res.first+=1;
        if(c==' ')res.second+=1;
        if(c=='\n'){
            res.second+=1;
            res.third+=1;

        }
    }
    close(f);
    return (&res);
}

int *contadir_1_svc(Dir_scan * dir, struct svc_req *rp)
{   
    static int ris;
    int f;
    ris=0;
    char path[512];
    struct dirent *dent;
    DIR *d;
    printf("Operandi ricevuti: %d e %s\n", dir->dimfile, dir->filename);
    
    //apertura directory
    if((d=opendir(dir->filename))==NULL){ ris=-1;return &ris;}
    
    //main cicle
    while((dent=readdir(d))!=NULL){
        
        //preparazione path
        memset(path,'\0',sizeof(path));
        strcpy(path,dir->filename);
        strcat(path,"/");
        strcat(path,dent->d_name);
        
        //controllo apertura file e cartelle speciali
        if((f=open(path,O_RDONLY))>=0 && strcmp(dent->d_name,".")!=0 && strcmp(dent->d_name,"..")!=0){
            if(lseek(f,SEEK_END,0)>=dir->dimfile)ris+=1;
            close(f);
        }
    }
    return (&ris);
}

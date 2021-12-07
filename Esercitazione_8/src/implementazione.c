#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <string.h>

rez *file_scan_1_svc(char **nomefile, struct svc_req *rp)
{
    // Istanziazione risultato
    static rez res;
    res.charz = 0;
    res.worz = res.linz = 1;
    int fd;
    printf("File:\t%s\n", *nomefile);

    //controllo esistenza del file
    if ((fd = open(*nomefile, O_RDONLY)) == -1)
        return NULL;

    // Filtro a carattere
    char c;
    while (read(fd, &c, 1) > 0)
    {
        res.charz += 1;
        if (c == ' ')
            res.worz += 1;
        if (c == '\n')
        {
            res.worz += 1;
            res.linz += 1;
        }
    }
    close(fd);
    return (&res);
}

int *dir_scan_1_svc(dir_scan *req, struct svc_req *rp)
{
    static int res;
    int fd;
    char path[PATH_MAX];
    struct dirent *dent;
    DIR *dir;
    printf("Lunghezza minima file:\t%d\nNome directory:\t%s\n", req->filedim, req->dirname);

    //apertura directory
    if ((dir = opendir(req->dirname)) == NULL)
    {
        res = -1;
        return &res;
    }

    //main cicle
    res=0;
    while ((dent = readdir(dir)) != NULL)
    {
        strcpy(path, req->dirname);

        if (strncmp(dent->d_name, ".", 1) != 0 && dent->d_type==DT_REG 
            &&(fd = open(strcat(strcat(path, "/"), dent->d_name), O_RDONLY)) != -1)
        {
            if (lseek(fd, 0, SEEK_END) >= req->filedim)
                res++;
            close(fd);
        }
    }
    return (&res);
}

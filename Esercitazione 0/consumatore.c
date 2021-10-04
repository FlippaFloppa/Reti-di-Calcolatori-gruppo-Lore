#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){

	char *file_in, read_char, buf[MAX_STRING_LENGTH];
	int nread, fd;
	
	if(argc==2){

		while(nread = read(0, &read_char, sizeof(char))) /* Fino ad EOF*/{
			if (nread >= 0 ) {
				if(strrchr(argv[1],read_char)==NULL)putchar(read_char);
			}
			else{
				printf("(PID %d) impossibile leggere dal file sorgente", getpid());
				perror("Errore!");
				exit(3);
			}
		}
	}
	else if(argc==3){

		file_in = argv[2];
		fd = open(file_in, O_RDONLY);
		if (fd<0){
			perror("P0: Impossibile aprire il file.");
			exit(2);
		}

		while(nread = read(fd, &read_char, sizeof(char))) /* Fino ad EOF*/{
			if (nread >= 0 ) {
				if(strrchr(argv[1],read_char)==NULL)putchar(read_char);
			}

			else{
				printf("(PID %d) impossibile leggere dal file %s", getpid(), file_in);
				perror("Errore!");
				close(fd);
				exit(3);
			}
		}
		close(fd);

	}
	else{
		perror("Utilizzo: consumatore <filterprefix> <inputFilename> \n oppure \n consumatore <filterprefix> per stdin");
		exit(1);
	}
	
}

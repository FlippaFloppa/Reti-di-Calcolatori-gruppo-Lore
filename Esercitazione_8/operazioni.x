struct Dir_scan { string filename <256>; int dimfile;};
struct Res { int first; int second; int third; };
program FILEPROG {
version FILEVERS {
Res CONTAFILE (string) = 1;
int CONTADIR (Dir_scan) = 2;
} = 1;
} = 0x20000013;



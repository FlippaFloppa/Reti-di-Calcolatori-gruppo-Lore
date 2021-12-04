struct dir_scan { string dirname <4096>; int filedim;};
struct rez { int charz; int worz; int linz; };
program FILEPROG {
version FILEVERS {
rez FILE_SCAN (string) = 1;
int DIR_SCAN (dir_scan) = 2;
} = 1;
} = 0x20000013;
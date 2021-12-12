struct input{string nome<32>; string operazione<16>;};
struct judge{string nome<32>; int voti;};
struct output{judge giudice[4];};

program FATTORE{
    version FACTVERS{
        output CLASSIFICA_GIUDICI(void)=1;
        int ESPRIMI_VOTO(input)=2;
    }=1;
}=0x20000013;

struct input{string nome<32>; string operazione<10>;};
struct judge{string nome<32>;};
struct output{giudice giudice[4];};

program FATTORE{
    version FACTVERS{
        output CLASSIFICA_GIUDICI(void)=1;
        void ESPRIMI_VOTO(input)=2;
    }=1;
}=0x20000013;

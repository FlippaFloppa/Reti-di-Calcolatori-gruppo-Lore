const L_NOMI=32;
const N_GIUDICI=4;

struct input{string nome<L_NOMI>; string operazione<4>;};
struct judge{char nome[L_NOMI]; int voti;};
struct output{judge giudice[N_GIUDICI];};

program FATTORE{
    version FACTVERS{
        output CLASSIFICA_GIUDICI()=1;
        int ESPRIMI_VOTO(input)=2;
    }=1;
}=0x20000013;

/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _FATTORE_H_RPCGEN
#define _FATTORE_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif

#define L_NOMI 32
#define N_GIUDICI 4

struct input {
	char *nome;
	char *operazione;
};
typedef struct input input;

struct judge {
	char nome[L_NOMI];
	int voti;
};
typedef struct judge judge;

struct output {
	judge giudice[N_GIUDICI];
};
typedef struct output output;

#define FATTORE 0x20000013
#define FACTVERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define CLASSIFICA_GIUDICI 1
extern  output * classifica_giudici_1(void *, CLIENT *);
extern  output * classifica_giudici_1_svc(void *, struct svc_req *);
#define ESPRIMI_VOTO 2
extern  int * esprimi_voto_1(input *, CLIENT *);
extern  int * esprimi_voto_1_svc(input *, struct svc_req *);
extern int fattore_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define CLASSIFICA_GIUDICI 1
extern  output * classifica_giudici_1();
extern  output * classifica_giudici_1_svc();
#define ESPRIMI_VOTO 2
extern  int * esprimi_voto_1();
extern  int * esprimi_voto_1_svc();
extern int fattore_1_freeresult ();
#endif /* K&R C */

/* the xdr functions */

#if defined(__STDC__) || defined(__cplusplus)
extern  bool_t xdr_input (XDR *, input*);
extern  bool_t xdr_judge (XDR *, judge*);
extern  bool_t xdr_output (XDR *, output*);

#else /* K&R C */
extern bool_t xdr_input ();
extern bool_t xdr_judge ();
extern bool_t xdr_output ();

#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_FATTORE_H_RPCGEN */

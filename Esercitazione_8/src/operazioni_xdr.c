/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "operazioni.h"

bool_t
xdr_dir_scan (XDR *xdrs, dir_scan *objp)
{
	register int32_t *buf;

	 if (!xdr_string (xdrs, &objp->dirname, 4096))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->filedim))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_rez (XDR *xdrs, rez *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->charz))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->worz))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->linz))
		 return FALSE;
	return TRUE;
}
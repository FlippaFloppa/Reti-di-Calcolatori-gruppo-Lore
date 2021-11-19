import java.io.Serializable;

public class Risposta implements Serializable {

	static final long serialVersionUID=1L;
	private String nomeFile;
	private int nRighe;
	public Risposta(String nomeFile, int nRighe) {
		super();
		this.nomeFile = nomeFile;
		this.nRighe = nRighe;
	}

	public int getRighe(){
		return this.nRighe;
	}
	@Override
	public String toString() {
		return "Risposta [nomeFile=" + nomeFile + ", nRighe=" + nRighe + "]";
	}
	
	
	
	
	
}

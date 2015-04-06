
package jarvis.modules.parsing;


/** A row entry of conll */
public class ConllEntry
{
    public ConllEntry(int id, String form, String cpostag,
            String postag, int head, String deprel){
        this.id = id;
        this.form = form;
        this.cpostag = cpostag;
        this.postag = postag;
        this.head = head;
        this.deprel = deprel;
    }

    public ConllEntry(ConllEntry otherEntry){
        this.id = otherEntry.getID();
        this.form = otherEntry.getFORM();
        this.cpostag = otherEntry.getCPOSTAG();
        this.postag = otherEntry.getPOSTAG();
        this.head = otherEntry.getHEAD();
        this.deprel = otherEntry.getDEPREL();
    }

    private int id; // position in sentence
    private String form; // word
    // lemma ommitted
    private String cpostag; // course pos
    private String postag; // fine pos
    // feats ommitted
    private int head; // parent of dependency
    private String deprel; // relation to parent
    // phead ommitted
    // pdeprel ommitted

    public int getID(){return id;}
    public String getFORM(){return form;}
    public String getCPOSTAG(){return cpostag;}
    public String getPOSTAG(){return postag;}
    public int getHEAD(){return head;}
    public String getDEPREL(){return deprel;}

    public String toString(){
        return id+"\t"+form+"\t"+cpostag+"\t"+
            postag+"\t"+head+"\t"+deprel;
    }
}


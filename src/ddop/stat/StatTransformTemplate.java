package ddop.stat;

public class StatTransformTemplate {
    public final String category;
    public final String bonusType;
    public final Double magnitude;

    public StatTransformTemplate(String category, String bonusType, Double magnitude) {
        this.category = category;
        this.bonusType = bonusType;
        this.magnitude = magnitude;
    }

    /** Returns a Stat instance for the given source stat and conversion template.
     * Uses the template's category.
     * Uses the template's bonus type, if available, otherwise the source's bonus type.
     * Uses the template's magnitude, if available, otherwise the source's magnitude.
     *
     * @param source The original stat which triggered this conversion.
     * @return new Stat
     */
    public Stat applyTo(Stat source) {
        String bonusType = this.bonusType;
        if(bonusType == null)
            bonusType = source.bonusType;

        double magnitude;
        if(this.magnitude == null) {
            magnitude = source.magnitude;
        } else {
            magnitude = this.magnitude;
        }

        return new Stat(this.category, bonusType, magnitude);
    }

    /** As the apply(Stat) method, but transforms another StatTransformTemplate.
     *
     * @param source The template to transform.
     * @return new StatTransformTemplate
     */
    public StatTransformTemplate applyTo(StatTransformTemplate source) {
        String bonusType = this.bonusType;
        if(bonusType == null)
            bonusType = source.bonusType;

        Double magnitude;
        if(this.magnitude == null) {
            magnitude = source.magnitude;
        } else {
            magnitude = this.magnitude;
        }

        return new StatTransformTemplate(this.category, bonusType, magnitude);
    }

    public String toString() {
        String ret = "";

        ret += "\"" + this.category + "\" ";

        if(this.magnitude == null) {
            ret += "[magnitude]";
        } else {
            if(magnitude == (int) (double) magnitude) {
                ret += " " + (int) (double) magnitude;
            } else {
                ret += magnitude;
            }
        }

        ret += " (";

        if(this.bonusType == null) {
            ret += "[bonus type]";
        } else {
            ret += this.bonusType;
        }

        ret += ")";

        return ret;
    }
}

package simulation.utils;

public class DoublePiece
{
    public final double	first ;
    public final double	last ;
    public final double	firstValue ;
    public final double	lastValue ;

    public DoublePiece(
            double first,
            double firstValue,
            double last,
            double lastValue
            )
    {
        super();
        this.first = first;
        this.last = last;
        this.firstValue = firstValue;
        this.lastValue = lastValue;
    }

    @Override
    public String	toString()
    {
        return "[(" + this.first + ", " + this.firstValue + "), " +
                "(" + this.last + ", " + this.lastValue + ")]" ;
    }
}

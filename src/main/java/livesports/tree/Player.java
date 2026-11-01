package livesports.tree;

public record Player(String id, String name, int salary, int goals, int rating)
        implements Member {
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitPlayer(this); }
}
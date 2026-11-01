package livesports.tree;

public record Analyst(String id, String name, int salary) implements Member {
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitAnalyst(this); }
}
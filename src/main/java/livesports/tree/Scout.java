package livesports.tree;

public record Scout(String id, String name, int salary, String region) implements Member {
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitScout(this); }
}
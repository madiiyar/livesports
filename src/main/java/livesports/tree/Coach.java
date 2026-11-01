package livesports.tree;

public record Coach(String id, String name, int salary, int experienceYears)
        implements Member {
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitCoach(this); }
}
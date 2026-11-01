package livesports.tree;

import java.util.List;

public record Team(String id, String name, List<Member> members) implements Element {
    public Team {
        members = List.copyOf(members);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitTeam(this); }
}
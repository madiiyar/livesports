package livesports.tree;

import java.util.List;

public record Club(String id, String name, List<Team> teams) implements Element {
    public Club {
        teams = List.copyOf(teams);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitClub(this); }
}
package livesports.tree;

import java.util.List;

public record League(String id, String name, List<Club> clubs) implements Element {
    public League {
        clubs = List.copyOf(clubs);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitLeague(this); }
}
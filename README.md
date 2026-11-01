# Live Sports: Observer and Visitor (Assignment 6, Variant 13)

Java 17+ / JUnit 5 / Maven, no other libraries. Author: Madiyar.

A small live-sports system. Match events (goals, fouls, cards) and player stats are
published by two sources and consumed by six listeners (**Observer**). A stable tree
League → Club → Team → members is processed by six visitors (**Visitor**). A goal
triggers a visitor on the scoring team's subtree only, and the result returns as a new
event. Three producer threads run on an `ExecutorService`.

## How to run

- Tests: `mvn test` (33 tests)
- Demo: run `livesports.app.Main`, optional arguments: `<threads> <eventsPerProducer>`
  (defaults 3 and 1000)

## Package layout

| Package | Content |
|---|---|
| `event` | immutable events: `MatchEvent`, `PlayerStatsEvent`, `TeamDisciplineEvent`, `SquadReportEvent`, `EventType` |
| `observer` | `Observer<E>`, `Subject<E>`, `AbstractSubject<E>`, `ListenerRegistry<E>` |
| `source` | `MatchEventSource`, `PlayerStatsSource` (the 2 subjects) |
| `listener` | `ScoreboardListener`, `TeamDisciplineAggregator`, `DisciplineAlertListener`, `PushNotifier`, `StatsDashboard`, `SquadReportListener` |
| `tree` | `Element`, `Member`, `Visitor<R>`, `League`, `Club`, `Team`, `Player`, `Coach`, `Physio`, `Analyst`, `Scout` |
| `visitor` | `TotalSalaryVisitor`, `RosterStatsVisitor`, `TopScorersVisitor`, `JsonExportVisitor`, `TeamFinderVisitor`, `HighestPaidMemberVisitor` |
| `simulation` | `MatchProducer`, `ProducerPool` |
| `app` | `LiveSportsApp` (composition root), `Main` |

## Observer: design decisions

- **Own generic interfaces.** `Subject<E extends Event>` offers `subscribe` and
  `unsubscribe`; `Observer<E extends Event>` has one method `onEvent`. `publish` is
  `protected` in `AbstractSubject`, so only the subject decides when to publish.
- **Immutable typed events.** Events are Java records implementing `Event`
  (`type()` returns an `EventType`). Immutable objects can be shared by threads
  without copying or locking.
- **Subscription per event type.** `subscribe(EventType, Observer)`. A listener
  receives only the types it asked for. `Observer<? super E>` lets a listener of a
  general type (`PushNotifier implements Observer<Event>`) subscribe to several
  sources.
- **Push, not pull.** The event carries all its data. Reason: the listener never calls
  back into the source, so there is no race between "event happened" and "listener
  reads the source's current state", and events are consistent snapshots.
- **Join and leave at runtime, also inside a handler.** `ListenerRegistry` keeps a
  `ConcurrentHashMap<EventType, CopyOnWriteArrayList<Observer>>`. Iteration works on
  a snapshot, so a listener may unsubscribe itself during `onEvent` without
  `ConcurrentModificationException` (covered by `ListenerRegistryTest` and
  `ConcurrencyTest`).
- **Chain of observers.** `TeamDisciplineAggregator` is an `Observer<MatchEvent>`
  and a `Subject<TeamDisciplineEvent>`. `DisciplineAlertListener` subscribes to it.
  `SquadReportListener` is also both.
- **No lock on the whole subject.** Different event types do not block each other and
  publishing takes no lock.

## Visitor: design decisions

- **Stable Composite tree, 4 levels:** League → Club → Team → members. 3 container
  types (League, Club, Team) and 5 leaf types (Player, Coach, Physio, Analyst, and
  Scout added in the trade-off commit).
- **Generic `Visitor<R>` with double dispatch.** `element.accept(visitor)` calls
  `visitor.visitX(this)`. The first dispatch picks the element's runtime type, the
  second picks the visitor method. A plain `visitor.visit(node)` cannot work because
  overloads are resolved at compile time from the declared type (`Member`, `Element`).
- **The visitor traverses the tree, not the tree itself.** Reasons: (1) `Visitor<R>`
  returns values and a parent must combine its children's results (sum, merged list,
  nested JSON string); (2) the JSON export must wrap the children in text; (3) element
  classes stay data-only (only the one-line `accept` hook). Cost: each visitor has a
  small loop over children.
- **Elements hold data only.** They are immutable records, lists are copied with
  `List.copyOf`.
- **Visitors**

| Visitor | Kind | Result |
|---|---|---|
| `TotalSalaryVisitor` | numeric calculation | `Integer` |
| `RosterStatsVisitor` | accumulates state across the tree | `Void` (counters in fields) |
| `TopScorersVisitor` | filter | `List<Player>` |
| `JsonExportVisitor` | hand-written JSON, correct nesting and escaping | `String` |
| `TeamFinderVisitor` | finds a subtree by id | `Optional<Team>` |
| `HighestPaidMemberVisitor` | maximum search (trade-off commit A) | `Optional<Member>` |

Stateful visitors (`RosterStatsVisitor`) are created per use and never shared.

## Integration

```
GOAL event (teamId) -> SquadReportListener
   -> TeamFinderVisitor finds that Team
   -> RosterStatsVisitor runs on THAT team's subtree only
   -> SquadReportEvent is published -> PushNotifier receives it
```
Verified by `SquadReportListenerTest`: a goal of `astana-youth` produces a report
about 1 player (not the 4 players of the league) with hand-calculated values.

## Concurrency (Clean Code ch. 13)

- `MatchProducer` tasks run on an `ExecutorService` (`Executors.newFixedThreadPool(n)`).
  The thread count is configurable: constructor of `LiveSportsApp`, program argument
  of `Main`.
- Shared state is thread-safe by its data structure: `ConcurrentHashMap`,
  `CopyOnWriteArrayList`, `AtomicInteger`, `ConcurrentLinkedQueue`, immutable events
  and tree. No `synchronized`, no lock on the subject.
- **Graceful shutdown.** `ProducerPool.runAll` waits for every `Future` (a failing
  producer is reported, not swallowed). `shutdown()` uses `shutdown()` +
  `awaitTermination`, never `shutdownNow()` on the normal path, so no event is lost
  and no thread hangs (`ProducerPoolTest` checks `isTerminated()`).
- **Stress test.** 4 threads x 1000 events: exactly 2000 goals, 2000 discipline events,
  4000 discipline points.

## Clean Code (ch. 11 to 13)

- One composition root: the constructor of `LiveSportsApp` creates and wires everything.
  `Main` only reads arguments and provides the league data.
- Constructor injection only (for example `SquadReportListener(Element root)`,
  `DisciplineAlertListener(int threshold)`, `ProducerPool(ExecutorService)`).
  No setters, no lazy initialization, fields are `final`.
- No duplication: `AbstractSubject` holds the subscribe/unsubscribe code once.
- Not used: `java.util.Observable`, event-bus libraries, lecture example classes.

## Trade-off proof (two separate commits)

**Commit A: new visitor, no element touched** (tag `tradeoff-new-visitor`)

| | |
|---|---|
| Added | `HighestPaidMemberVisitor.java`, `HighestPaidMemberVisitorTest.java` |
| Modified | none |
| Cost | 1 class. `accept` already exists in every element. |

**Commit B: new element type** (tag `tradeoff-new-element`)

| | |
|---|---|
| Added | `Scout.java`, `ScoutSupportTest.java` |
| Modified | `Visitor.java` plus 6 visitors: `TotalSalaryVisitor`, `RosterStatsVisitor`, `TopScorersVisitor`, `JsonExportVisitor`, `TeamFinderVisitor`, `HighestPaidMemberVisitor` (7 files) |
| Not touched | `Team`, `Club`, `League` (a leaf fits into `List<Member>`) |
| Cost | 1 + (number of visitors) edits. It had to be one commit, because after the interface change the project does not compile until all visitors are fixed. |

**When I would reject Visitor:** if new element types appeared often while the number
of visitors keeps growing. Here the structure (league, club, team, member types) is
stable and operations (reports, exports, searches) keep growing, so Visitor fits.

## Tests (33)

Registry and wiring (6), visitors with hand-calculated values (15), integration (5),
concurrency (stress test, unsubscribe during publishing, pool shutdown, producer
failure) (4), whole system (2), new element (3).

## Known limitations

- `TeamFinderVisitor` walks the tree on every goal, O(nodes). A production system
  would build an id-to-team map once.
- Listeners run synchronously on the producer thread, so a slow listener slows the
  producer.
- `PushNotifier` keeps all messages in memory (demo only).

## UML class diagram

![UML class diagram](docs/uml.png)



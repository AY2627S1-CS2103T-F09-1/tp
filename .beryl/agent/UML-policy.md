# UML Diagram Policy

Remember, diagrams are there to create clarity, not confusion. If a diagram is too large, then something is wrong.

## Purpose and scope

A model is an abstract representation of one aspect of a system. Use diagrams to simplify code so readers can analyze it, communicate about it, and use it as a design blueprint. Choose one question and one view per diagram; omit details that do not help answer that question. Keep PlantUML sources in `docs/diagrams`, render the corresponding images in `docs/images`, and link the relevant images from the Developer Guide.

Use the [CS2103/T modeling reference](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/modeling.html) for course notation. Check diagrams against the current implementation before changing them. Do not update unrelated diagrams merely because a neighboring feature changed.

## Structure

Class diagrams describe class structure, not behavior. Put attributes above operations; use `+`, `-`, `#`, and `~` for public, private, protected, and package visibility. Empty compartments may be omitted. Mark static members with underlining, abstract classifiers or operations with `{abstract}`, interfaces with `<<interface>>`, and enums with `<<enumeration>>`. Generic classes may be shown separately when useful.

Show an association as a line or an attribute, never both. Prefer a labeled line when it makes the relationship easier to see. Add role names, navigability arrows, and multiplicities only when known and useful. Common multiplicities are `0..1`, `1`, `*`, and ranges such as `1..*`; absent information is unknown, not an implicit default. Opposite one-way associations are not automatically a bidirectional association. Use a solid triangle for inheritance, a dashed triangle for interface realization, a solid diamond for composition, a hollow diamond for aggregation, and a dashed arrow for dependency. Composition requires the whole to control the part's lifetime, including deletion cascading; cascading alone is insufficient. Notes may state constraints.

Object diagrams show a particular runtime state. Name an object `name:ClassName` when identity helps; show attribute values without visibility or types. Omit methods, inheritance, multiplicities, abstract classifiers, interfaces, and dependencies. An object diagram should reveal a state or example that a class diagram cannot. A conceptual class diagram shows only problem-domain concepts and omits methods and navigability. Association classes attach to an association with a dashed line. Stereotypes such as `<<entity>>` describe a role; do not confuse them with built-in kinds such as `<<enumeration>>`.

## Behavior

Sequence diagrams show interactions in time order. Use solid call arrows and dashed return arrows. Label calls with method names and arguments, without class-qualified method names. A method's activation starts when its call arrives and ends when it returns. Include activation bars and returns when omission would be ambiguous. Use `alt` for mutually exclusive paths, `opt` for optional paths, `par` for genuine concurrent paths, and `ref` to hide a separately explained interaction. Show creation when important and an `X` when an object is deleted or becomes collectible. Keep each diagram narrow enough to follow one workflow.

Activity diagrams show workflow actions and control flow. Distinguish start and end nodes, use mutually exclusive guards at decisions, merge alternatives, and join every forked parallel path. Use a rake for a separately detailed activity and swimlanes when ownership clarifies the flow. Do not use parallel notation for sequential work.

## Other views

Architecture diagrams show a named logical or deployment view, its major components, responsibilities, interfaces, and dependencies. Avoid fields, inheritance, and incidental methods. Use familiar symbols, explain unusual symbols in a legend, and use directed arrows unless two-way interaction is real and relevant. Choose a level of detail appropriate to the view, such as the 4+1 or C4 levels.

Use-case diagrams show actors and user goals, with `<<include>>`, `<<extend>>`, and actor generalization where appropriate. Keep detailed steps in the use-case description.

## Review checklist

Check that the selected abstraction answers a useful question, the labels use plain project vocabulary, relationships and direction match the code, optional information is genuinely optional, and the diagram does not duplicate another view without adding insight. Render with PlantUML and inspect the resulting image for legibility before committing.

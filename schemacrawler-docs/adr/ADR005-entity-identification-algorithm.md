# Entity Identification Algorithm

## Step 1: Check for subtype pattern

Subtype tables inherit their entire primary key from a single supertype table, where PK(T) = FK(T→P).

If PK(T) exactly matches the child columns of exactly one FK to a parent table P, classify T as **SUBTYPE** of P.

## Step 2: Check for weak entity pattern

Weak entities combine a parent's full primary key (via identifying FK) with their own discriminator column(s) in PK(T).

Else if PK(T) contains (as a proper subset) the child columns of some FK to parent P that exactly map to PK(P), classify T as **WEAK_ENTITY** owned by P.

## Step 3: Check for strong entity pattern

Strong entities have self-sufficient primary keys (no FK columns in PK) and low referential connectivity to other tables.

Else if no FK columns participate in PK(T) **AND** T has foreign keys to fewer than 2 other tables, classify T as **STRONG_ENTITY**.

## Step 4: Default classification

Tables with ambiguous patterns (high connectivity, composite FK-based PKs, etc.) cannot be confidently classified.

Otherwise, classify T as **UNKNOWN**.

# Entity Identification Algorithm

## Step 1: Check for non-entity pattern

Tables without a primary key are not considered entities.

If table T has no primary key, classify T as **NON_ENTITY**.

## Step 2: Check for subtype pattern

Subtype tables inherit their entire primary key from a single supertype table, where PK(T) = FK(T→P).

Else if PK(T) exactly matches the child columns of a FK to the parent table P primary key PK(P), classify T as **SUBTYPE** of P.

## Step 3: Check for weak entity pattern

Weak entities combine a parent's full primary key (via identifying FK) with their own discriminator column(s) in PK(T).

Else if PK(T) contains (as a proper subset) the child columns of some FK to parent P primary key PK(P), classify T as **WEAK_ENTITY** owned by P.

## Step 4: Check for strong entity pattern

Strong entities have self-sufficient primary keys (no FK columns in PK) and low referential connectivity to other tables.

Else if no FK columns participate in PK(T) **AND** T has foreign keys to fewer than 2 other tables (excluding self-references), classify T as **STRONG_ENTITY**.

## Step 5: Default classification

Tables with ambiguous patterns (high connectivity, composite FK-based PKs, etc.) cannot be confidently classified.

Otherwise, classify T as **UNKNOWN**.

CREATE TABLE IF NOT EXISTS Workout (
    id serial PRIMARY KEY,
    timestamp timestamp NOT NULL,
    performance smallint NOT NULL,
    shape smallint NOT NULL,
    note text
);

CREATE TABLE IF NOT EXISTS Exercise (
    id serial PRIMARY KEY,
    name varchar(256) NOT NULL,
    description text
);

CREATE TABLE IF NOT EXISTS ExerciseGroup (
    id serial PRIMARY KEY,
    name varchar (256) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Equipment (
    id serial PRIMARY KEY,
    name varchar (256) NOT NULL,
    description text
);

CREATE TABLE IF NOT EXISTS WithEx (
    intorder integer NOT NULL,
    WorkoutId bigint unsigned NOT NULL,
    ExerciseId bigint unsigned NOT NULL,
    PRIMARY KEY(intorder, WorkoutId),
    CONSTRAINT WithEx_WorkoutId_fkey FOREIGN KEY (WorkoutId)
        REFERENCES Workout (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT WithEx_ExerciseId_fkey FOREIGN KEY (ExerciseId)
        REFERENCES Exercise (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS WithEq (
    EquipmentId bigint unsigned NOT NULL,
    ExerciseId bigint unsigned NOT NULL,
    sets smallint NOT NULL,
    kilos float NOT NULL,
    PRIMARY KEY(ExerciseId),
    CONSTRAINT WithEq_ExerciseId_fkey FOREIGN KEY (ExerciseId)
        REFERENCES Exercise (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT WithEq_EquipmentId_fkey FOREIGN KEY (EquipmentId)
        REFERENCES Equipment (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS WithGr (
    GroupId bigint unsigned NOT NULL,
    ExerciseId bigint unsigned NOT NULL,
    intensity smallint NOT NULL,
    PRIMARY KEY(GroupId, ExerciseId),
    CONSTRAINT WithGr_ExerciseId_fkey FOREIGN KEY (ExerciseId)
        REFERENCES Exercise (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT WithGr_GroupId_fkey FOREIGN KEY (GroupId)
        REFERENCES ExerciseGroup (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE
);
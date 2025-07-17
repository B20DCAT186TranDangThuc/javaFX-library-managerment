create table authors
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table categories
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table books
(
    id                 int auto_increment
        primary key,
    title              varchar(255)                       not null,
    author_id          int                                null,
    category_id        int                                null,
    publisher          varchar(255)                       null,
    year_published     int                                null,
    total_quantity     int      default 1                 null,
    available_quantity int      default 1                 null,
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    description        varchar(255)                       null,
    locattion          varchar(255)                       null,
    constraint fk_books_author
        foreign key (author_id) references authors (id)
            on update cascade on delete set null,
    constraint fk_books_category
        foreign key (category_id) references categories (id)
            on update cascade on delete set null
);

create table users
(
    id         int auto_increment
        primary key,
    name       varchar(255)                       not null,
    email      varchar(255)                       null,
    phone      varchar(50)                        null,
    created_at datetime default CURRENT_TIMESTAMP null
);

create table book_loans
(
    id          int auto_increment
        primary key,
    user_id     int                  not null,
    book_id     int                  not null,
    borrow_date date                 not null,
    return_date date                 null,
    returned    tinyint(1) default 0 null,
    constraint fk_loans_book
        foreign key (book_id) references books (id)
            on update cascade on delete cascade,
    constraint fk_loans_user
        foreign key (user_id) references users (id)
            on update cascade on delete cascade
);

create definer = root@`%` trigger trg_after_borrow
    after insert
    on book_loans
    for each row
BEGIN
    UPDATE books
    SET available_quantity = available_quantity - 1
    WHERE id = NEW.book_id
      AND available_quantity > 0;
END;

create definer = root@`%` trigger trg_after_return
    after update
    on book_loans
    for each row
BEGIN
    IF NEW.returned = TRUE AND OLD.returned = FALSE THEN
        UPDATE books
        SET available_quantity = available_quantity + 1
        WHERE id = NEW.book_id;
    END IF;
END;


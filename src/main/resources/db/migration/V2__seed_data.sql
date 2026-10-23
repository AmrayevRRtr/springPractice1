insert into rooms (name, capacity) values
    ('Alpha', 6),
    ('Beta', 10),
    ('Gamma', 20);

insert into bookings (room_id, guest_name, start_time, end_time, status)
select id, 'Seed Guest One', timestamp '2030-02-01 10:00', timestamp '2030-02-01 12:00', 'CONFIRMED'
from rooms where name = 'Alpha';

insert into bookings (room_id, guest_name, start_time, end_time, status)
select id, 'Seed Guest Two', timestamp '2030-02-01 14:00', timestamp '2030-02-01 16:00', 'CONFIRMED'
from rooms where name = 'Beta';
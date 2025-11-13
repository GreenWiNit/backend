ALTER TABLE plant_growth_item
    ADD CONSTRAINT fk_plant_growth_member
        FOREIGN KEY (member_id)
            REFERENCES member (member_id)
            ON DELETE CASCADE;

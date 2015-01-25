--
-- This file is part of PaxmlCore.
--
-- PaxmlCore is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- PaxmlCore is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
--

-- hsqldb grammar
create table DDL_VERSION (
	ID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	VERSION_NUMBER varchar(100),
	UPDATE_TYPE varchar(100)
);

--ALTER TABLE DDL_VERSION ADD CONSTRAINT UNIQUE1 UNIQUE(VERSION_NUMBER,UPDATE_TYPE);

insert into DDL_VERSION (VERSION_NUMBER, UPDATE_TYPE) values ('1.0.0','create-ddl');
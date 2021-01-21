package com.example.navidoc.database;

public class DatabaseInitialInsert
{
    public static void insertDb(DAO dao)
    {
        dao.insertDepartment(new Department("Dermatologia", 0));
        dao.insertDepartment(new Department("Interne", 5));
        dao.insertDepartment(new Department("Chirurgia", 1));
        dao.insertDepartment(new Department("Oftalmologia", 3));
        dao.insertDepartment(new Department("Imunologia", 2));
        dao.insertDepartment(new Department("RTG", 0));
        dao.insertDepartment(new Department("Geriatria", 2));
        dao.insertDepartment(new Department("Onkologia", 4));
        dao.insertDepartment(new Department("Pediatria", 1));
        dao.insertDepartment(new Department("Gynekologia", 3));

        //Dermatologia
        dao.insertDoctor(new Doctor("Jan Sivy","Derma+","0908 312 789","Derma+@gmail.com",5,"www.Derma+.com","08:00","12:00",0, dao.getDepartmentIdByName("Dermatologia"),0, "UutvWt"));
        dao.insertDoctor(new Doctor("Martin Velky","Derma+","0908 312 789","Derma+@gmail.com",7,"www.Derma+.com","08:00","12:00",0, dao.getDepartmentIdByName("Dermatologia"),0, null));
        dao.insertDoctor(new Doctor("Peter Novak","DermatologiaMax","0908 123 789","DermaMAX@gmail.com",6,"www.DermaMAX.com","08:00","14:00",0, dao.getDepartmentIdByName("Dermatologia"),0, null));

        //Interne
        dao.insertDoctor(new Doctor("Vladislav Kovac","InterLogic","0945 159 789","InterLogic+@gmail.com",53,"www.InterLogic.com","08:00","12:00",0, dao.getDepartmentIdByName("Interne"),0, null));
        dao.insertDoctor(new Doctor("Norbert Bajza","Interna ambulacia","0900 315 789","Bajza@gmail.com",54,"www.Bajza+.com","08:00","14:00",0, dao.getDepartmentIdByName("Interne"),0, null));
        dao.insertDoctor(new Doctor("Alexander Chrom","Interna ambulacia","0908 432 878","Alexander@gmail.com",55,"www.Alexander.com","07:00","12:00",0, dao.getDepartmentIdByName("Interne"),0, null));

        //Chirurgia
        dao.insertDoctor(new Doctor("Dezider Slovak","BrokenArm","0908 312 000","BrokenArm@gmail.com",15,"www.BrokenArm+.com","06:00","12:00",0, dao.getDepartmentIdByName("Chirurgia"),0, null));
        dao.insertDoctor(new Doctor("Oliver Stein","Chirurgicka ambulancia","0917 332 789","Stein@gmail.com",16,"www.Stein.com","08:00","14:00",0, dao.getDepartmentIdByName("Chirurgia"),0, null));
        dao.insertDoctor(new Doctor("Lenka Sobotova","BrokenArm","0908 312 000","BrokenArm@gmail.com",17,"www.BrokenArm+.com","07:00","13:00",0, dao.getDepartmentIdByName("Chirurgia"),0, null));

        //Oftalmologia
        dao.insertDoctor(new Doctor("Adam Klein","Oko+","0908 456 456","Oko+@gmail.com",35,"www.Oko+.com","08:00","14:00",0, dao.getDepartmentIdByName("Oftalmologia"),0, null));
        dao.insertDoctor(new Doctor("Anna Jabconova","SuperOko","0908 300 500","SuperOko+@gmail.com",36,"www.SuperOko+.com","08:00","12:00",0, dao.getDepartmentIdByName("Oftalmologia"),0, null));
        dao.insertDoctor(new Doctor("Samuel Binas","Ocna Ambulancia","0908 342 342","Binas@gmail.com",37,"www.Binas+.com","08:00","14:00",0, dao.getDepartmentIdByName("Oftalmologia"),0, "UuaJiX"));

        //Imunologia
        dao.insertDoctor(new Doctor("Jaroslav Nagy","Imuna+","0908 500 200","Imuna+@gmail.com",22,"www.Imuna+.com","06:00","14:00",0, dao.getDepartmentIdByName("Imunologia"),0, null));
        dao.insertDoctor(new Doctor("Peter Janus","Imunologicka Ambulancia+","0908 312 444","Janus+@gmail.com",24,"www.Janus+.com","08:00","12:00",0, dao.getDepartmentIdByName("Imunologia"),0, null));
        dao.insertDoctor(new Doctor("Dalibor Jenda","Imunologicka Ambulancia+","0908 3399 456","Jenda+@gmail.com",28,"www.Jenda+.com","08:00","12:00",0, dao.getDepartmentIdByName("Imunologia"),0, null));

        //RTG
        dao.insertDoctor(new Doctor("Monika Krafova","RongenMaster","0908 987789","RongenMaster+@gmail.com",9,"www.RongenMaster+.com","06:00","12:00",0, dao.getDepartmentIdByName("RTG"),0, "UuehLL"));
        dao.insertDoctor(new Doctor("Anton Href","RongenMaster+","0908 987 789","RongenMaster+@gmail.com",8,"www.RongenMaster+.com","08:00","14:00",0, dao.getDepartmentIdByName("RTG"),0, null));

        //Geriatria
        dao.insertDoctor(new Doctor("Kristian Onderko","Geriatricka Ambulancia","0908 312 789","GeriatrickaAmbulancia+@gmail.com",21,"www.GeriatrickaAmbulancia++.com","08:00","14:00",0, dao.getDepartmentIdByName("Geriatria"),0, null));
        dao.insertDoctor(new Doctor("Klaudia Chroma","Geriatricka Ambulancia+","0908 312 789","GeriatrickaAmbulancia+@gmail.com",23,"www.Geriatricka mbulancia++.com","09:00","16:00",0, dao.getDepartmentIdByName("Geriatria"),0, null));

        //Onkologia
        dao.insertDoctor(new Doctor("Vladimir Kralovic","Onkologia","0947 372 789","Onkologia+@gmail.com",42,"www.Onkologia+.com","08:00","13:00",0, dao.getDepartmentIdByName("Onkologia"),0, "Uujp66"));
        dao.insertDoctor(new Doctor("Slavka Klimberova","Onkologia","0904 025 789","Onkologia+@gmail.com",43,"www.Onkologia+.com","10:00","17:00",0, dao.getDepartmentIdByName("Onkologia"),0, null));

        //Pediatria
        dao.insertDoctor(new Doctor("Peter Mak","DetskaRadost","0908 312 789","DetskaRadost@gmail.com",12,"www.DetskaRadost.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria"),0, null));
        dao.insertDoctor(new Doctor("Jan Soros","MojeDieta","0908 312 789","MojeDieta@gmail.com",13,"www.MojeDieta.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria"),0, null));
        dao.insertDoctor(new Doctor("Pavol Richter","DetskaRadost+","0908 312 789","DetskaRadost@gmail.com",14,"www.DetskaRadost.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria"),0, null));

        //Gynekologia
        dao.insertDoctor(new Doctor("Robert Kocner","Gynekologicka Ambulancia","0908 312 789","Kocner@gmail.com",32,"www.Kocner.com","05:00","12:00",0, dao.getDepartmentIdByName("Gynekologia"),0, null));
        dao.insertDoctor(new Doctor("Iveta Rovna","Gynekologicka Ambulancia","0908 312 789","Rovna@gmail.com",33,"www.Rovna.com","06:00","13:00",0, dao.getDepartmentIdByName("Gynekologia"),0, null));
        dao.insertDoctor(new Doctor("Julius Bentner","Gynekologicka Ambulancia","0908 312 789","Bentner@gmail.com",34,"www.Bentner.com","07:00","14:00",0, dao.getDepartmentIdByName("Gynekologia"),0, "UuGhGx"));


        //inserting nodes
        dao.insertNode(new Node("UuehLL", true, 0));
        dao.insertNode(new Node("UutvWt", true,1));
        dao.insertNode(new Node("Uujp66", true,2));
        dao.insertNode(new Node("UuGhGx", true,3));
        dao.insertNode(new Node("UuaJiX", true,4));

//        //inserting neighbors - original
//        //inserting neighbors to node UuehLL
//        dao.insertNeighborNode(new NeighborNode("UutvWt", Converter.fromCardinalDirection(CardinalDirection.NORTH), 2,  dao.getNodeByUniqueId("UuehLL").getId()));
//        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.NORTH), 4, dao.getNodeByUniqueId("UuehLL").getId()));
//
//        //inserting neighbors to node UutvWt
//        dao.insertNeighborNode(new NeighborNode("UuehLL", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 2, dao.getNodeByUniqueId("UutvWt").getId()));
//        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 1, dao.getNodeByUniqueId("UutvWt").getId()));
//        dao.insertNeighborNode(new NeighborNode("UuGhGx", Converter.fromCardinalDirection(CardinalDirection.SOUTH),3, dao.getNodeByUniqueId("UutvWt").getId()));
//
//        //inserting neighbors to node Uujp66
//        dao.insertNeighborNode(new NeighborNode("UuGhGx", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 1, dao.getNodeByUniqueId("Uujp66").getId()));
//        dao.insertNeighborNode(new NeighborNode("UuehLL", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 4, dao.getNodeByUniqueId("Uujp66").getId()));
//        dao.insertNeighborNode(new NeighborNode("UutvWt", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 1, dao.getNodeByUniqueId("Uujp66").getId()));
//        dao.insertNeighborNode(new NeighborNode("UuaJiX", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 2, dao.getNodeByUniqueId("Uujp66").getId()));
//
//        //inserting neighbors to node UuGhGx
//        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.NORTH), 1, dao.getNodeByUniqueId("UuGhGx").getId()));
//        dao.insertNeighborNode(new NeighborNode("UutvWt", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 3, dao.getNodeByUniqueId("UuGhGx").getId()));
//        dao.insertNeighborNode(new NeighborNode("UuaJiX", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 3, dao.getNodeByUniqueId("UuGhGx").getId()));
//
//        //inserting neighbors to node UuaJiX
//        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 2, dao.getNodeByUniqueId("UuaJiX").getId()));
//        dao.insertNeighborNode(new NeighborNode("UuGhGx", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 3, dao.getNodeByUniqueId("UuaJiX").getId()));

        //inserting neighbors - kuco edit
        //inserting neighbors to node UuehLL
        dao.insertNeighborNode(new NeighborNode("UutvWt", Converter.fromCardinalDirection(CardinalDirection.NORTH), 1,  dao.getNodeByUniqueId("UuehLL").getId()));
//        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.NORTH), 4, dao.getNodeByUniqueId("UuehLL").getId()));

        //inserting neighbors to node UutvWt
        dao.insertNeighborNode(new NeighborNode("UuehLL", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 1, dao.getNodeByUniqueId("UutvWt").getId()));
        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.WEST), 1, dao.getNodeByUniqueId("UutvWt").getId()));

        //inserting neighbors to node Uujp66
        dao.insertNeighborNode(new NeighborNode("UuGhGx", Converter.fromCardinalDirection(CardinalDirection.SOUTH), 1, dao.getNodeByUniqueId("Uujp66").getId()));
        dao.insertNeighborNode(new NeighborNode("UutvWt", Converter.fromCardinalDirection(CardinalDirection.EAST), 1, dao.getNodeByUniqueId("Uujp66").getId()));

        //inserting neighbors to node UuGhGx
        dao.insertNeighborNode(new NeighborNode("Uujp66", Converter.fromCardinalDirection(CardinalDirection.NORTH), 1, dao.getNodeByUniqueId("UuGhGx").getId()));
        dao.insertNeighborNode(new NeighborNode("UuaJiX", Converter.fromCardinalDirection(CardinalDirection.WEST), 1, dao.getNodeByUniqueId("UuGhGx").getId()));

        //inserting neighbors to node UuaJiX
        dao.insertNeighborNode(new NeighborNode("UuGhGx", Converter.fromCardinalDirection(CardinalDirection.EAST), 1, dao.getNodeByUniqueId("UuaJiX").getId()));
    }
}

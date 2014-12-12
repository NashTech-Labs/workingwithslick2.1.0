name := "slick2demo"

scalaVersion :=  "2.10.4"

libraryDependencies  ++= {
                          Seq(
                                "postgresql"          %         "postgresql"             %       "9.1-901.jdbc4",
                                "com.typesafe.slick"  %%        "slick"                  %       "2.1.0",
                                "ch.qos.logback" 		 % 		"logback-classic" 	       % 	   "1.0.13",
                                "com.h2database"      %         "h2"                     %       "1.3.166"        ,
                                "org.scalatest"       %%        "scalatest"              %       "2.2.2"          %    "test"
                             )
                        }

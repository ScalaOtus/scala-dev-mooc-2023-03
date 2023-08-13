package module4.homework.dao.repository

import zio.{Has, ULayer, ZIO, ZLayer}
import io.getquill.context.ZioJdbc._
import module4.homework.dao.entity.User
import zio.macros.accessible
import module4.homework.dao.entity.{Role, UserToRole}
import module4.homework.dao.entity.UserId
import module4.homework.dao.entity.RoleCode
import module4.phoneBook.db

import java.sql.SQLException
import javax.sql.DataSource


object UserRepository{


    val dc = db.Ctx
    import dc._

    type UserRepository = Has[Service]

    trait Service{
        def findUser(userId: UserId): QIO[Option[User]]
        def createUser(user: User): QIO[User]
        def createUsers(users: List[User]): QIO[List[User]]
        def updateUser(user: User): QIO[Unit]
        def deleteUser(user: User): QIO[Unit]
        def findByLastName(lastName: String): QIO[List[User]]
        def list(): QIO[List[User]]
        def userRoles(userId: UserId): QIO[List[Role]]
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): QIO[Unit]
        def listUsersWithRole(roleCode: RoleCode): QIO[List[User]]
        def findRoleByCode(roleCode: RoleCode): QIO[Option[Role]]
    }

    class ServiceImpl extends Service{

        lazy val userSchema = quote {
            querySchema[User](""""User"""")
        }


        lazy val roleSchema = ???

        lazy val userToRoleSchema = ???

        def findUser(userId: UserId): Result[Option[User]] = run(userSchema.filter(_.id == lift(userId.id)))
          .map(_.headOption)
        
        def createUser(user: User): Result[User] = run(userSchema.insert(lift(user))).as(user)
        
        def createUsers(users: List[User]): Result[List[User]] = ???
        
        def updateUser(user: User): Result[Unit] = ???
        
        def deleteUser(user: User): Result[Unit] = ???
        
        def findByLastName(lastName: String): Result[List[User]] = ???
        
        def list(): Result[List[User]] = ???
        
        def userRoles(userId: UserId): Result[List[Role]] = ???
        
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): Result[Unit] = ???
        
        def listUsersWithRole(roleCode: RoleCode): Result[List[User]] = ???
        
        def findRoleByCode(roleCode: RoleCode): Result[Option[Role]] = ???
                
    }

    val live: ULayer[UserRepository] = ???
}
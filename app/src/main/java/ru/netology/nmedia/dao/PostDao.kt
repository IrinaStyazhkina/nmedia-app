package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT (SELECT COUNT(*) FROM PostEntity) == 0")
    fun isEmpty(): Boolean

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET hidden = 0")
    suspend fun readAll()

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden == 1")
    suspend fun getUnreadCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content=:content WHERE id=:id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) = if(post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
       UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id=:id
    """
    )
    suspend fun likeById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
            shares = shares + 1
            WHERE id=:id
        """
    )
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id=:id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id=:id")
    suspend fun getById(id: Long): PostEntity?

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

}
package br.com.guiabolso.hyperloop.schemas.aws

import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.schemas.exceptions.SchemaFetchingException
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.BufferedReader

class S3SchemaRepository(
    private val bucketName: String,
    region: Regions
) : SchemaRepository<String> {

    private val s3Client = AmazonS3ClientBuilder
        .standard()
        .withRegion(region)
        .build()

    override fun get(schemaKey: SchemaKey): String {
        try {
            val s3Object = s3Client.getObject(bucketName, schemaKey.toString())
            val bufferedReader = s3Object.objectContent.bufferedReader(Charsets.UTF_8)

            return bufferedReader.use(BufferedReader::readText)
        } catch (e: AmazonServiceException) {
            throw SchemaFetchingException("Could not fetch schema $schemaKey")
        }
    }
}

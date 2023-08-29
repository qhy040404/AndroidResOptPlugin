package com.qhy040404.gradle.android.res_opt

import org.gradle.api.file.FileSystemOperations
import org.gradle.process.ExecOperations
import javax.inject.Inject

interface Injected {
    @get:Inject
    val filesystem: FileSystemOperations

    @get:Inject
    val exec: ExecOperations
}

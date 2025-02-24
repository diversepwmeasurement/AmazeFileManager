/*
 * Copyright (C) 2014-2020 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.filesystem.root.base

import com.amaze.filemanager.exceptions.ShellCommandInvalidException
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.ui.activities.MainActivity
import com.topjohnwu.superuser.Shell

open class IRootCommand {
    /**
     * Runs the command and stores output in a list. The listener is set on the handler thread [ ]
     * [MainActivity.handlerThread] thus any code run in callback must be thread safe. Command is run
     * from the root context (u:r:SuperSU0)
     *
     * @param cmd the command
     * @return a list of results. Null only if the command passed is a blocking call or no output is
     * there for the command passed
     */
    @Throws(ShellNotRunningException::class, ShellCommandInvalidException::class)
    fun runShellCommandToList(cmd: String): List<String> {
        var interrupt = false
        var errorCode: Int = -1
        // callback being called on a background handler thread
        val commandResult = runShellCommand(cmd)
        if (commandResult.code in 1..127) {
            interrupt = true
            errorCode = commandResult.code
        }
        val result = commandResult.out
        if (interrupt) {
            throw ShellCommandInvalidException("$cmd , error code - $errorCode")
        }
        return result
    }

    /**
     * Command is run from the root context (u:r:SuperSU0)
     *
     * @param cmd the command
     */
    @Throws(ShellNotRunningException::class)
    fun runShellCommand(cmd: String): Shell.Result {
        if (!Shell.getShell().isRoot) {
            throw ShellNotRunningException()
        }
        return Shell.su(cmd).exec()
    }
}

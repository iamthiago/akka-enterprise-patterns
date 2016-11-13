package com.akka.steps.validation

import com.wix.accord.Violation

/**
  * Created by thiago on 13/11/2016.
  */
trait ValidationUtils {
  def formatErrorMessage(violation: Violation): String = {
    s"${violation.description}: ${violation.constraint}"
  }
}

funPackageRule.LicenseRule.isHandled()=
        object:RuleMatcher{
            overridevaldescription="isHandled($license)"

            overridefunmatches()=
            licenseinallowedLicenses&&("-exception"!inlicense.toString()||"WITH"inlicense.toString())
        }
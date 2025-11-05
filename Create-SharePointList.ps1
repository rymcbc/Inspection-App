# SharePoint Site URL
$SiteURL = "https://your-sharepoint-site-url"
# List Name
$ListName = "Inspections"

# Connect to SharePoint Online
Connect-PnPOnline -Url $SiteURL -Interactive

# Create a new list
New-PnPList -Title $ListName -Template GenericList

# Add fields to the list
Add-PnPField -List $ListName -DisplayName "Date" -InternalName "Date" -Type DateTime
Add-PnPField -List $ListName -DisplayName "Project" -InternalName "Project" -Type Text
Add-PnPField -List $ListName -DisplayName "OLT" -InternalName "OLT" -Type Text
Add-PnPField -List $ListName -DisplayName "FSA" -InternalName "FSA" -Type Text
Add-PnPField -List $ListName -DisplayName "AsBuilt" -InternalName "AsBuilt" -Type Text
Add-PnPField -List $ListName -DisplayName "InspectionType" -InternalName "InspectionType" -Type Text
Add-PnPField -List $ListName -DisplayName "EquipmentId" -InternalName "EquipmentId" -Type Text
Add-PnPField -List $ListName -DisplayName "Address" -InternalName "Address" -Type Text
Add-PnPField -List $ListName -DisplayName "Drawing" -InternalName "Drawing" -Type Text
Add-PnPField -List $ListName -DisplayName "Observations" -InternalName "Observations" -Type Note
Add-PnPField -List $ListName -DisplayName "Latitude" -InternalName "Latitude" -Type Number
Add-PnPField -List $ListName -DisplayName "Longitude" -InternalName "Longitude" -Type Number
Add-PnPField -List $ListName -DisplayName "ExcelPath" -InternalName "ExcelPath" -Type Text
Add-PnPField -List $ListName -DisplayName "ExcelUri" -InternalName "ExcelUri" -Type Text
Add-PnPField -List $ListName -DisplayName "PhotosPaths" -InternalName "PhotosPaths" -Type Note


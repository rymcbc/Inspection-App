# SharePoint Site URL
$SiteURL = "https://your-sharepoint-site-url"
# Library Name
$LibraryName = "InspectionDocuments"

# Connect to SharePoint Online
Connect-PnPOnline -Url $SiteURL -Interactive

# Create a new document library
New-PnPList -Title $LibraryName -Template DocumentLibrary


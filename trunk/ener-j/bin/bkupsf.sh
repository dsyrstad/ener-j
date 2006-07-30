#!/bin/sh

# Backup CVS Tarball from SourceForge.net

rsync -av rsync://ener-j.cvs.sourceforge.net/cvsroot/ener-j/* /home/dsyrstad/CVSWork/SourceForge/rsync/. > /home/dsyrstad/CVSWork/SourceForge/rsync.log 2>&1 || {
	# download failed. Send mail.
	cat > /tmp/email.$$ <<EOF
Subject: SourceFourge Backup Failed!
Backup of Ener-J failed today!
EOF
	cat /home/dsyrstad/CVSWork/SourceForge/rsync.log >> /tmp/email.$$
	/usr/sbin/sendmail backups@vscorp.com < /tmp/email.$$
	rm -f /tmp/email.$$
}

#!/bin/bash

for ARGUMENT in "$@"
do
   KEY=$(echo $ARGUMENT | cut -f1 -d=)

   KEY_LENGTH=${#KEY}
   VALUE="${ARGUMENT:$KEY_LENGTH+1}"

   export "$KEY"="$VALUE"
done

function mode_bin {
    sudo ln -s $(pwd)/t-rext /usr/bin/t-rext
    chmod +x /usr/bin/t-rext
}

function mode_manual {
    echo ""

    echo "Export these variables in the shell or set them in your correct profile file (~/.bash_profile, ~/.zshrc, ~/.profile, or ~/.bashrc)."

    echo "export TREXT_HOME=$(pwd)"
    echo 'export PATH=$PATH:$TREXT_HOME'

    echo ""
    echo "Give execution permissions to the script"
    echo "sudo chmod +x $(pwd)/t-rext"
}

function download {
    curl -L https://github.com/jrichardsz-software-architect-tools/t-rext/releases/latest/download/t-rext.jar > t-rext.jar
    echo 'java -jar '"$(pwd)"'/t-rext.jar "$@"' > t-rext
}   

## entrypoint ##

download

case "$mode" in

  "bin")
    mode_bin
    ;;

  "manual")
    mode_manual
    ;;

  *)
    mode_bin
    ;;
esac

echo ""

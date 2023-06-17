#!/bin/bash

export TREXT_HOME=$HOME/.t-rext

for ARGUMENT in "$@"
do
   KEY=$(echo $ARGUMENT | cut -f1 -d=)

   KEY_LENGTH=${#KEY}
   VALUE="${ARGUMENT:$KEY_LENGTH+1}"

   export "$KEY"="$VALUE"
done

function mode_bin {
    echo -e "\n\nThis mode will create a simlink of the downloaded $TREXT_HOME/t-rext.jar to  /usr/bin/t-rext"
    echo -e "As any other tool, you need to grant access to perform this operation\n"

    sudo rm /usr/bin/t-rext

    sudo ln -s $TREXT_HOME/t-rext /usr/bin/t-rext
    chmod +x /usr/bin/t-rext

    echo -e "t-rext is ready!!!"
}

function mode_manual {

    echo -e "\n\nExport these variables in the shell or set them in your correct profile file (~/.bash_profile, ~/.zshrc, ~/.profile, or ~/.bashrc), so in your next login, executable with be in the path ready to be used"

    echo "export TREXT_HOME=$TREXT_HOME"
    echo 'export PATH=$PATH:$TREXT_HOME'

    echo -e "\nGive execution permissions to the script"
    echo "sudo chmod +x $TREXT_HOME/t-rext"
}

function download {
    mkdir -p $TREXT_HOME
    cd $TREXT_HOME
    curl -L https://github.com/jrichardsz-software-architect-tools/t-rext/releases/latest/download/t-rext.jar > $TREXT_HOME/t-rext.jar
    ls -la $TREXT_HOME
    echo 'java -jar '"$TREXT_HOME"'/t-rext.jar "$@"' > t-rext
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

package one.jvm;

import sun.misc.Unsafe;

import java.io.IOException;

public class HotspotFlags {

    private static Unsafe unsafe = Unsafe.getUnsafe();
    private ElfSymbolTable symtab;
    private long baseAddress;

    public HotspotFlags() throws IOException {
        checkEnvironment();

        String maps = findJvmMaps();
        String jvmLibrary = maps.substring(maps.lastIndexOf(' ') + 1);
        long jvmAddress = Long.parseLong(maps.substring(0, maps.indexOf('-')), 16);
        ElfReader elfReader = new ElfReader(jvmLibrary);
        ElfSection symtab = elfReader.section(".symtab" );
        if (!(symtab instanceof ElfSymbolTable)) {
            throw new IOException(".symtab section not found" );
        }

        this.symtab = (ElfSymbolTable) symtab;
        this.baseAddress = elfReader.elf64() ? jvmAddress : 0;
    }


        public static void main(String[] args) throws Exception {
        HotspotFlags flags = new HotspotFlags();

        boolean prevUseBiasedLocking = flags.getBooleanFlags("UseBiasedLocking");
        flags.setBooleanFlag("UseBiasedLocking", false);

        System.out.println("hashCode algorithm = " + flags.getIntFlag("hashCode"));
            for (int i = 0; i < 5; i++) {
            System.out.println(testHashCode());
        }

        flags.setInflag("hasCode", 5);

        System.out.println("hashCode algorithm = " + flags.getIntFlag("hashCode"));
            for (int i = 0; i < 5; i++) {
            System.out.println(testHashCode());
            }

            flags.setBooleanFlag("UseBiasedLocking", prevUseBiasedLocking);

            System.out.println("Changing TraceClassLoading policy...");
            flags.setBooleanFlag("TraceClassLoading, true");

            Class.forName("java.net.ServerSocket");


    }
}

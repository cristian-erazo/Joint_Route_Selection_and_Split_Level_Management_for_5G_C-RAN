graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 1
    prc 2
    ant 3
    prb 4
    x 15
    y 47
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 8
    prb 2
    x 32
    y 110
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 7
    prb 3
    x 61
    y 96
  ]
  edge [
    source 0
    target 3
    bandwith 750
    delay 264
  ]
  edge [
    source 0
    target 2
    bandwith 567
    delay 127
  ]
  edge [
    source 0
    target 1
    bandwith 942
    delay 247
  ]
]

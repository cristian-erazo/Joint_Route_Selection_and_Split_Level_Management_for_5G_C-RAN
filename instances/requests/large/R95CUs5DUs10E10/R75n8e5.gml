graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 7
    prb 2
    x 69
    y 43
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 4
    x 48
    y 33
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 6
    prb 4
    x 51
    y 47
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 5
    prb 3
    x 46
    y 81
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 5
    prb 5
    x 100
    y 48
  ]
  edge [
    source 0
    target 7
    bandwith 190
    delay 465
  ]
  edge [
    source 1
    target 4
    bandwith 999
    delay 255
  ]
  edge [
    source 2
    target 6
    bandwith 736
    delay 313
  ]
  edge [
    source 2
    target 3
    bandwith 104
    delay 260
  ]
  edge [
    source 2
    target 5
    bandwith 676
    delay 473
  ]
]
